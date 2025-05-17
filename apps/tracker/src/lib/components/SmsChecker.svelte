<script lang="ts">
  import {checkPermissions, onSmsReceived, requestPermissions} from "tauri-plugin-sms-api"
  import {onMount} from "svelte"
  import {load, Store} from "@tauri-apps/plugin-store"
  import type {Settings} from "$lib/types"

  let store: Store
  let settings = $state<Settings | undefined>()

  onMount(() => {
    load('store.json', {autoSave: true})
      .then(async s => {
        store = s
        settings = await store.get<Settings>('settings')
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

  let sms = $state('')
  onSmsReceived(({body, from}) => {
    if (!settings || !body.startsWith(settings.prefix)) return
    console.log('sms received', body)
    sms = JSON.stringify(from)
    log.push('Received SMS: from ${from} - ${body}')
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

<div>
  SMS: {sms}
</div>

<div>

</div>

