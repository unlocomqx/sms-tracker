<script lang="ts">
  import LocationChecker from "../lib/components/LocationChecker.svelte"
  import SmsChecker from "$lib/components/SmsChecker.svelte"

  let password = $state()
  let show_password = $state(false)
</script>

<main>

  Password:

  <input bind:value={password} id="password" name="password" required type={show_password ? 'text' : 'password'}>

  <button onclick={() => show_password = !show_password} type="button">
    {show_password ? 'Hide' : 'Show'}
  </button>

  <button>Save</button>

  <LocationChecker/>

  <SmsChecker/>

</main>
