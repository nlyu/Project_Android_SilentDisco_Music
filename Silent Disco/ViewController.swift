//
//  ViewController.swift
//  Silent Disco
//
//  Created by Daniel Edrisian on 5/7/19.
//  Copyright © 2019 Daniel Edrisian. All rights reserved.
//

import UIKit
import Firebase
import FirebaseCore
import FirebaseDatabase
import FirebaseAuth

class ViewController: UIViewController {

    @IBOutlet weak var emailTF : UITextField!
    @IBOutlet weak var passTF : UITextField!

    @IBOutlet weak var loginBtn: UIButton!
    @IBOutlet weak var registerBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        emailTF.borderStyle = .roundedRect
        passTF.borderStyle = .roundedRect
        
        loginBtn.onTap {
            Auth.auth().signIn(withEmail: self.emailTF.text!, password: self.passTF.text!) { [weak self] user, error in
                guard let strongSelf = self else { return }

            }
        }
        
        registerBtn.onTap {
            Auth.auth().createUser(withEmail: self.emailTF.text!, password: self.passTF.text!) { authResult, error in

            }
        }
        
        registerBtn.layer.borderWidth = 2
        registerBtn.layer.borderColor = #colorLiteral(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
    }
    
    
}



class ViewController2: UIViewController {
    @IBOutlet weak var createBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        createBtn.layer.borderColor = #colorLiteral(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
    }
    
}

class ViewController3: UIViewController {
    
    @IBOutlet weak var nameTF : UITextField!
    @IBOutlet weak var songTF : UITextField!
    @IBOutlet weak var genreTF : UITextField!
    
    @IBOutlet weak var createBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameTF.borderStyle = .roundedRect
        songTF.borderStyle = .roundedRect
        genreTF.borderStyle = .roundedRect
        
    }
    
    
}
