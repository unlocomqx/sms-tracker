import {
  checkPermissions as checkPluginPermissions,
  requestPermissions as requestPluginPermissions
} from '@tauri-apps/api/core'

export async function checkPermissions(): Promise<{ sms: string }> {
  return checkPluginPermissions('sms')
}

export async function requestPermissions() : Promise<{ sms: string }>{
  return requestPluginPermissions('sms')
}
