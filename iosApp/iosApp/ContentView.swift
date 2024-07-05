import SwiftUI
import UIKit
import shared.com.grappim.aipal

struct ContentView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> some UIViewController {
        MainViewControllerKt.MainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoreSafeArea(.keyboard)
    }
}
