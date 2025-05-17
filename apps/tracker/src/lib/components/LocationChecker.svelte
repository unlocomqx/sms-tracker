<script lang="ts">
  import {checkPermissions, getCurrentPosition, type Position, requestPermissions} from "@tauri-apps/plugin-geolocation"

  let location_permission = $state<PermissionState | "prompt-with-rationale">()

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

  let current_pos_state = $state('idle')
  let current_pos = $state<Position>()

  async function testGetLocation() {
    console.log('start')
    try {
      current_pos_state = 'getting location...'
      current_pos = undefined
      current_pos = await getCurrentPosition({
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 5000,
      })
      current_pos_state = 'idle'
    } catch (e) {
      console.error(e)
      current_pos_state = 'An error occurred while getting the location.'
    }
    console.log('end')
  }
</script>

<div>
  {#await checkLocationPermissions() then location_permission}
    {#if location_permission !== "granted"}
      <button onclick={requestLocationPermissions}>Request permissions</button>
    {:else}
      <button onclick={testGetLocation}>Test location</button>

      {#if current_pos_state !== 'idle'}
        <div>
          {current_pos_state}
        </div>
      {/if}

      {#if current_pos}
        <div>
          {JSON.stringify(current_pos, null, 2)}
        </div>
      {/if}
    {/if}
  {/await}
</div>

