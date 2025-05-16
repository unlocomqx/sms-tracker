<script lang="ts">
  import {checkPermissions, getCurrentPosition, type Position, requestPermissions} from "@tauri-apps/plugin-geolocation"

  let password = $state()
  let show_password = $state(false)

  let location_permission = $state<PermissionState | "prompt-with-rationale">("denied")

  async function checkLocationPermissions() {
    const {location} = await checkPermissions()
    location_permission = location
    return location
  }

  async function requestLocationPermissions() {
    const {location} = await requestPermissions(['location'])
    location_permission = location
    return location
  }

  let current_pos = $state<Position>()

  async function testGetLocation() {
    current_pos = await getCurrentPosition()
  }
</script>

<main>

  Password:

  <input bind:value={password} id="password" name="password" required type={show_password ? 'text' : 'password'}>

  <button onclick={() => show_password = !show_password} type="button">
    {show_password ? 'Hide' : 'Show'}
  </button>

  <button>Save</button>

  <div>
    {#if location_permission !== "granted"}
      <button onclick={() => requestLocationPermissions()}>Request permissions</button>
    {:else}
      <button onclick={() => testGetLocation()}>Test location</button>

      {#if current_pos}
        <div>
          {JSON.stringify(current_pos)}
        </div>
      {/if}
    {/if}
  </div>

</main>
