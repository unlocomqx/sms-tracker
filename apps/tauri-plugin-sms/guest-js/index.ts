import {
  addPluginListener,
  checkPermissions as checkPluginPermissions,
  invoke,
  PluginListener
} from '@tauri-apps/api/core'

const plugin_name = 'sms'

export async function checkPermissions(): Promise<{ sms: string }> {
  return checkPluginPermissions(plugin_name)
}

export async function requestPermissions(): Promise<{ sms: string }> {
  return invoke('plugin:geolocation|request_permissions', {
    permissions: ['sms']
  })
}

export async function onSmsReceived(
  handler: ({body, from}: { body: string, from: string }) => void
): Promise<PluginListener> {
  return await addPluginListener(
    plugin_name,
    'sms-received',
    handler
  )
}
