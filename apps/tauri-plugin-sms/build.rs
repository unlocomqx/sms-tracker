const COMMANDS: &[&str] = &["check_permissions", "request_permissions", "register_listener", "registerListener"];

fn main() {
  tauri_plugin::Builder::new(COMMANDS)
    .android_path("android")
    .ios_path("ios")
    .build();
}
