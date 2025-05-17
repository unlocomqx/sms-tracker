import {checkPermissions, getCurrentPosition, type Position, requestPermissions} from "@tauri-apps/plugin-geolocation"

export async function getCurrentLocation(): Promise<Position | undefined> {
  const {location} = await checkPermissions()
  if (location !== 'granted') {
    const {location} = await requestPermissions(['location'])
    if (location !== 'granted') {
      return
    }
  }

  const current_location = await getCurrentPosition({
    enableHighAccuracy: true,
    timeout: 10000,
    maximumAge: 5000,
  })

  return current_location
}
