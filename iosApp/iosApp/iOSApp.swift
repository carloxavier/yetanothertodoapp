import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        DriverProviderKt.driver = DriverFactory().createDriver()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
