<script lang="ts">
  import type {Sms} from "tauri-plugin-sms-api"
  import {checkPermissions, onSmsReceived, requestPermissions} from "tauri-plugin-sms-api"
  import {onMount} from "svelte"
  import {load, Store} from "@tauri-apps/plugin-store"
  import type {Settings} from "$lib/types"
  import {getCurrentLocation} from "$lib/location"
  import {sendSMS, validateSms} from "$lib/sms"

  let store: Store
  let settings = $state<Settings | undefined>()

  onMount(() => {
    load('store.json', {autoSave: true})
      .then(async s => {
        store = s
      })
  })

  let sms_permission = $state<string>('denied')

  async function checkSmsPermissions() {
    console.log('start check permissions')
    const {sms} = await checkPermissions()
    console.log({sms})
    sms_permission = sms
    return sms
  }

  async function requestSmsPermissions() {
    console.log('request permissions')
    const {sms} = await requestPermissions()
    console.log('result', sms)
    sms_permission = sms
    return sms
  }

  onSmsReceived(async (sms: Sms) => {
    const {body, from} = sms
    settings = await store.get<Settings>('settings')

    if (!settings) {
      log.push(`No settings found, please set a password then try again.`)
      return
    }

    const clean_body = body.replace(settings.password, '***')

    const {valid, reason} = validateSms(sms, settings)

    if (!valid) {
      log.push(`Received invalid SMS from ${from} - ${clean_body}: ${reason}`)
      return
    }

    log.push(`Received SMS: from ${from} - ${clean_body}`)

    log.push(`Getting location...`)
    const location = await getCurrentLocation()
    if (!location) {
      log.push(`Unable to get location`)
      return
    }
    log.push(`Location: ${location.coords.longitude}, ${location.coords.latitude}`)
    log.push(`Sending location...`)

    const success = await sendSMS(from, `${settings.password} gps: [${location.coords.longitude},${location.coords.latitude}]`)
    if (success) {
      log.push(`Location sent to ${from}`)
    } else {
      log.push(`Unable to send location`)
    }
  })

  let log = $state<Array<string>>([])
</script>

<div>
  -- {JSON.stringify(sms_permission)} --
  {#await checkSmsPermissions() then _}
    {#if sms_permission !== "granted"}
      <button onclick={requestSmsPermissions}>Request permissions</button>
    {:else}
      :tick: Granted
    {/if}
  {/await}
</div>

<pre>{log.join('\n')}</pre>

<style>
    pre {
        white-space: pre-wrap;
        border: 1px solid gray;
        padding: 1rem;
    }
</style>
