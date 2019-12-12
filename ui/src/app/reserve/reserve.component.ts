import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { backend_url } from '../global';

@Component({
  selector: 'app-form',
  templateUrl: './reserve.component.html',
  styleUrls: ['./reserve.component.css']
})
export class ReserveComponent implements OnInit {


  borrowForm: FormGroup;
  submitted = false;

  constructor(private formBuilder: FormBuilder,private http: HttpClient) { }

  private onSubmit() {
    this.submitted = true;

    //post request body
    let body = new URLSearchParams();
    body.append('plateNo', this.borrowForm.value.plateNumber);
    body.append('pickUpDate', this.borrowForm.value.pickUpDate.split('-')[2]);
    body.append('pickUpMonth', this.borrowForm.value.pickUpDate.split('-')[1]);
    body.append('pickUpYear', this.borrowForm.value.pickUpDate.split('-')[0]);
    body.append('dropOffDate', this.borrowForm.value.dropOffDate.split('-')[2]);
    body.append('dropOffMonth', this.borrowForm.value.dropOffDate.split('-')[1]);
    body.append('dropOffYear', this.borrowForm.value.dropOffDate.split('-')[0]);

    //set post sending type
    let header = {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')};

    //send data to backend
    this.http.post(backend_url+'/item/reserve',body.toString(),header).subscribe(
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
      this.borrowForm = this.formBuilder.group({
        plateNumber: ['', [Validators.required]],
        pickUpDate: ['', Validators.required],
        dropOffDate: ['', Validators.required]
      });
  }

  // convenience getter for easy access to form fields
  get f() { return this.borrowForm.controls; }

}
