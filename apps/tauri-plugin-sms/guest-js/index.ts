import {
  addPluginListener,
  checkPermissions as checkPluginPermissions,
  PluginListener,
  requestPermissions as requestPluginPermissions
} from '@tauri-apps/api/core'

const plugin_name = 'sms'

export async function checkPermissions(): Promise<{ sms: string }> {
  return checkPluginPermissions(plugin_name)
}

export async function requestPermissions() : Promise<{ sms: string }>{
  return requestPluginPermissions(plugin_name)
}

export async function onSmsReceived(
  handler: (url: string) => void
): Promise<PluginListener> {
  return await addPluginListener(
    plugin_name,
    'sms-received',
    handler
  );
}
