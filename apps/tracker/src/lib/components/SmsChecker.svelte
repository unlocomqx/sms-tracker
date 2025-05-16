<script lang="ts">
  import {checkPermissions, requestPermissions} from "tauri-plugin-sms-api"

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
    const permission = await requestPermissions()
    console.log('result', permission)
    sms_permission = permission
    return permission
  }
</script>

<div>
  -- {JSON.stringify(sms_permission)} --
  {#await checkSmsPermissions() then _}
    {#if sms_permission !== "grantedaaa"}
      <button onclick={() => requestSmsPermissions()}>Request permissions</button>
    {:else}
      :tick: Granted
    {/if}
  {/await}
</div>

