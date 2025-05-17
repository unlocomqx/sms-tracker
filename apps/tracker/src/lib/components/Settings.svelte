<script lang="ts">
  import {load, Store} from "@tauri-apps/plugin-store"
  import type {Settings} from "$lib/types"
  import {onMount} from "svelte"

  let store: Store
  let settings = $state<Settings>({
    from: '',
    password: ''
  })
  let show_password = $state(false)
  onMount(() => {
    load('store.json', {autoSave: true})
      .then(async s => {
        store = s
        settings = (await store.get<Settings>('settings') || settings)
      })
  })

  async function saveSettings() {
    await store.set('settings', settings)
    await store.save()
  }
</script>

<div>

  <div>
    <label for="from">Allow from numbers (comma separated):</label>
    <input bind:value={settings.from} id="from" name="from" placeholder="22222222,33333333">
    <p>
      Leave empty to allow from any numbers (not recommended).
    </p>
  </div>

  <div>
    <label for="password">Password:</label>

    <input bind:value={settings.password} id="password" name="password" required
           type={show_password ? 'text' : 'password'}>

    <button onclick={() => show_password = !show_password} type="button">
      {show_password ? 'Hide' : 'Show'}
    </button>
  </div>

  <div>
    <button onclick={saveSettings}>Save</button>
  </div>

</div>
