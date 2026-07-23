import UIKit
import SwiftUI
import YamsApp

struct ComposeView: UIViewControllerRepresentable {
    let diceAssistantBridge: PlayIosDiceAssistantBridge

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            diceAssistantBridge: diceAssistantBridge
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    private let diceAssistantBridge = DiceAssistantBridge()

    var body: some View {
        ComposeView(diceAssistantBridge: diceAssistantBridge)
            .ignoresSafeArea()
    }
}
