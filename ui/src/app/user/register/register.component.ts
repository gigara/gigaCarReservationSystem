import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { backend_url } from '../../global';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
    submitted = false;

    constructor(private formBuilder: FormBuilder,private http: HttpClient) { }

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            name: ['', Validators.required],
            number: ['', [Validators.required,Validators.maxLength(9),Validators.minLength(9)]],
            email: ['', [Validators.required, Validators.email]],
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    onSubmit() {
        this.submitted = true;

            //post request body
            let body = new URLSearchParams();
            body.append('name', this.registerForm.value.name);
            body.append('mobNumber', this.registerForm.value.number);
            body.append('email', this.registerForm.value.email);

        //set post sending type
    let header = {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')};
   
    //send data to backend
    this.http.post(backend_url+'/reader/add',body.toString(),header).subscribe(
        response => {
            let httpResponse = JSON.parse(JSON.stringify(response));
            alert(httpResponse.status);
        },
        error => {
            console.log("Error", error);
        }
    );
    }

}
