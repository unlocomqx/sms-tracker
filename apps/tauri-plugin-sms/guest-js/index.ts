import {
  addPluginListener,
  checkPermissions as checkPluginPermissions,
  invoke,
  PluginListener
} from '@tauri-apps/api/core'

const plugin_name = 'sms'

export type Sms = {
  from: string
  body: string
}

export async function checkPermissions(): Promise<{ sms: string }> {
  return checkPluginPermissions(plugin_name)
}

export async function requestPermissions(): Promise<{ sms: string }> {
  return invoke('plugin:geolocation|request_permissions', {
    permissions: ['sms']
  })
}

export async function onSmsReceived(
  handler: (sms: Sms) => void
): Promise<PluginListener> {
  return await addPluginListener(
    plugin_name,
    'sms-received',
    handler
  )
}

export async function sendSms(to: string, body: string): Promise<{ success: boolean }> {
  return invoke('plugin:sms|send_sms', {
    to,
    body
  })
}
