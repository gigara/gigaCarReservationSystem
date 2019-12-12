import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { backend_url } from '../../global';

@Component({
  selector: 'app-return-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class ReturnFormComponent implements OnInit {


  returnForm: FormGroup;
  submitted = false;

  constructor(private formBuilder: FormBuilder,private http: HttpClient) { }

  private onSubmit() {
    this.submitted = true;

    //post request body
    var plateNo  =  this.returnForm.value.plateNumber;
    var pickUpDate =  this.returnForm.value.pickUpDate.split('-')[2];
    var pickUpMonth =  this.returnForm.value.pickUpDate.split('-')[1];
    var pickUpYear =  this.returnForm.value.pickUpDate.split('-')[0];
    var dropOffDate =  this.returnForm.value.dropOffDate.split('-')[2];
    var dropOffMonth =  this.returnForm.value.dropOffDate.split('-')[1];
    var dropOffYear =  this.returnForm.value.dropOffDate.split('-')[0];

    //set post sending type
    let header = {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')};

    //send data to backend
    this.http.get(backend_url+'/item/isAvailable/' + plateNo + '/' + pickUpDate + '/' + pickUpMonth + '/' + pickUpYear + '/' + dropOffDate + '/' + dropOffMonth + '/' + dropOffYear).subscribe(
        response => {
            let httpResponse = JSON.parse(JSON.stringify(response));
            alert(httpResponse.status);
        },
        error => {
            console.log("Error", error);
        }
    );

}

  ngOnInit() {
    this.returnForm = this.formBuilder.group({
      plateNumber: ['', [Validators.required]],
      pickUpDate: ['', Validators.required],
      dropOffDate: ['', Validators.required]
    });
}

// convenience getter for easy access to form fields
get f() { return this.returnForm.controls; }

}
