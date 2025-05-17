<script lang="ts">
  import type {Sms} from "tauri-plugin-sms-api"
  import {checkPermissions, onSmsReceived, requestPermissions} from "tauri-plugin-sms-api"
  import {onMount, type SvelteComponent} from "svelte"
  import {load, Store} from "@tauri-apps/plugin-store"
  import type {Coords, Settings} from "$lib/types"
  import {getCurrentLocation} from "$lib/location"
  import {getSmsCommand, sendSMS, validateSms} from "$lib/sms"
  import Map from "$lib/map/Map.svelte"
  import CenterMarker from "$lib/map/CenterMarker.svelte"

  let store: Store
  let settings = $state<Settings>({
    target: '',
    from: '',
    password: ''
  })

  let latitude = $state(0)
  let longitude = $state(0)
  let mapComp: (SvelteComponent & {
    centerMap: (coords: Coords) => void
  }) | undefined = $state()

  onMount(() => {
    load('settings.json', {autoSave: true})
      .then(async s => {
        store = s
        settings = (await store.get<Settings>('settings') || settings)
      })
  })

  let sms_permission = $state<string>('denied')

  async function checkSmsPermissions() {
    const {sms} = await checkPermissions()
    sms_permission = sms
    return sms
  }

  async function requestSmsPermissions() {
    const {sms} = await requestPermissions()
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

    const command = getSmsCommand(body)

    if (!command) {
      log.push('No sms command found: use this format: password command')
      return
    }

    log.push('Received command: ' + command)

    switch (command) {
      case 'gps':
        log.push(`Getting location...`)
        const location = await getCurrentLocation()
        if (!location) {
          log.push(`Unable to get location`)
          return
        }
        log.push(`Location: ${location.coords.latitude},${location.coords.longitude}`)
        log.push(`Sending location...`)

        const success = await sendSMS(from, `${settings.password} gps: [${location.coords.latitude},${location.coords.longitude}]`)
        if (success) {
          log.push(`Location sent to ${from}`)
        } else {
          log.push(`Unable to send location`)
        }
        break

      case 'gps:':
        log.push('Reading received location...')
        const new_location: Array<string> = body.split(' ')[2]?.replace('[', '')?.replace(']', '')?.split(',') ?? []
        latitude = Number(new_location?.[0])
        longitude = Number(new_location?.[1])
        log.push(`Location: ${new_location?.[0]}, ${new_location?.[1]}`)
        break
    }
  })

  let log = $state<Array<string>>([])
  let log_div = $state<HTMLPreElement>()
  $effect(() => {
    if (log.length) {
      log_div?.scrollTo(0, log_div.scrollHeight)
    }
  })
</script>

<div>
  {#await checkSmsPermissions() then _}
    {#if sms_permission !== "granted"}
      <button onclick={requestSmsPermissions}>Request permissions</button>
    {:else}
      :tick: Granted
    {/if}
  {/await}
</div>

<pre bind:this={log_div}>{log.join('\n')}</pre>

{#if latitude && longitude}
  <Map bind:this={mapComp} center={{ latitude, longitude }}>
    <CenterMarker coords={{ latitude, longitude }}/>
  </Map>
{/if}

<div style="margin-top: 1rem;">
  <div>
    <label for="from">Get device location</label>
    <input bind:value={settings.target} id="from" name="from" onblur={() => store.set('settings', settings)}
           placeholder="22222222">
    <p>
      Enter the number of the device you want to track.
    </p>

    <button onclick={async () => {
      const success = await sendSMS(settings.target, `${settings.password} gps`)
      if (success) {
        log.push(`Location request sent to ${settings.target}`)
      } else {
        log.push(`Unable to send location request`)
      }
    }}>Send location request
    </button>
  </div>
</div>

<style>
    pre {
        white-space: pre-wrap;
        border: 1px solid gray;
        padding: 1rem;
        max-height: 200px;
        overflow-y: auto;
        box-sizing: border-box;
    }
</style>
