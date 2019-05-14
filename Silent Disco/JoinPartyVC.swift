//
//  ViewController.swift
//  Silent Disco
//
//  Created by Daniel Edrisian on 5/7/19.
//  Copyright © 2019 Daniel Edrisian. All rights reserved.
//

import UIKit
import GooglePlaces
import Firebase
import FirebaseDatabase

class JoinPartyVC: UIViewController {
    
    @IBOutlet weak var segment: UISegmentedControl!
    
    @IBOutlet weak var passTF: UITextField!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var trendingTV: UITableView!
    
    @IBOutlet weak var allTV: UITableView!
    
    let partyData = [("Far party", "13 peeps, No, too far"),
                     ("tyygaa", "7 peeps, pop, 0km"),
                     ("nlyu's", "4 peeps, hip hop, 1km"),
                     ("demo tmw ohoho", "5 peeps, 1km"),
                     ("Kill the lights", "1 peeps, 1km")]
    
    let partyDataAlph = [("A", "1 peeps, 0km"),
                         ("AliceTest", "1 peeps, 0km"),
                         ("Amortized", "1 peeps, 0km"),
                         ("Berkeley", "1 peeps, 0km"),
                         ("cool party", "1 peeps, 0km"),
                         ("Demo", "1 peeps, 0km"),
                         ("Demo", "1 peeps, 0km"),
                         ("demo tmw ohoho", "5 peeps, 1km"),
                         ("Kill the lights", "1 peeps, 1km"),
                         ("night in the bay", "1 peeps, 0km"),
                         ("nlyu's", "4 peeps, hip hop, 1km"),
                         ("test party", "1 peeps, 0km"),
                         ("tyygaa", "7 peeps, pop, 0km")]
    
    override func viewDidLoad() {
        super.viewDidLoad()

        trendingTVInit()
        allTVInit()
        let font = UIFont.systemFont(ofSize: 16)
        segment.setTitleTextAttributes([NSAttributedString.Key.font: font],
                                                for: .normal)
    }
    
    func trendingTVInit() {
        
        trendingTV.numberOfSectionsIn { () -> Int in
            return 1
        }
        
        trendingTV.numberOfRows { (_) -> Int in
            return self.partyData.count
        }
        
        trendingTV.cellForRow { (indexPath) -> UITableViewCell in
            let cell = self.trendingTV.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
            cell.textLabel!.text = self.partyData[indexPath.row].0
            cell.detailTextLabel!.text = self.partyData[indexPath.row].1
            return cell
        }
        
        trendingTV.didSelectRowAt { (indexPath) in
            print("hmm")
        }
    }
    
    func allTVInit() {
        
        allTV.numberOfSectionsIn { () -> Int in
            return 1
        }
        
        allTV.numberOfRows { (_) -> Int in
            return self.partyDataAlph.count
        }
        
        allTV.cellForRow { (indexPath) -> UITableViewCell in
            let cell = self.allTV.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
            cell.textLabel!.text = self.partyDataAlph[indexPath.row].0
            cell.detailTextLabel!.text = self.partyDataAlph[indexPath.row].1
            return cell
        }
        
        allTV.didSelectRowAt { (indexPath) in
            print("hmm")
        }
    }

}

//////////////
//..........//
//......... //
//.../////////
//.../////////
//..........//
//......... //
//.../////////
//.../////////
//.../////////
//.../////////
//.../////////
//.../////////
//.../////////
//////////////
