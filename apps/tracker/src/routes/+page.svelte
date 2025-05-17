<script lang="ts">
  import LocationChecker from "../lib/components/LocationChecker.svelte"
  import SmsChecker from "$lib/components/SmsChecker.svelte"
  import Settings from "$lib/components/Settings.svelte"
</script>

<main>

  <Settings/>

  <LocationChecker/>

  <SmsChecker/>

</main>
