import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        DependencyProvider.shared.driver = DriverFactory().createDriver()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
