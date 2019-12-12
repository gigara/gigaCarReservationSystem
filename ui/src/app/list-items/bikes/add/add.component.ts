import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormBuilder, FormControl, Validators, FormArray } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { backend_url } from '../../../global';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  styleUrls: ['./add.component.css']
})
export class AddBikeComponent implements OnInit {

  addNewBikeForm: FormGroup;
  responseMsg: String;

  constructor(public activeModal: NgbActiveModal,private formBuilder: FormBuilder,private http:HttpClient) { }

  closeModal() {
    this.activeModal.close('Modal Closed');
  }

  submitForm() {
        //post request body
        let body = new URLSearchParams();
        body.append('plateNo', this.addNewBikeForm.value.plateNumber);
        body.append('make', this.addNewBikeForm.value.make);
        body.append('model', this.addNewBikeForm.value.model);
        body.append('noOfGears', this.addNewBikeForm.value.gears);
        body.append('isWithABS', this.addNewBikeForm.value.ABS);
        body.append('type', "bike");

        //set post sending type
        let header = {headers: new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')};

        //hide the form and display loading
    (document.querySelector('form') as HTMLElement).className = 'transform-bounce';
    setTimeout( () => { (document.querySelector('form') as HTMLElement).className = 'transform-close'; }, 700 );

    setTimeout( () => {
        (document.querySelector('.LoaderBalls') as HTMLElement).style.display = 'flex';
    //sending data to the backend
    this.http.post(backend_url+"/item/add" ,body.toString(),header)
    .subscribe(
            response => {
                //hide loader
                (document.querySelector('.LoaderBalls') as HTMLElement).style.display = 'none';

            setTimeout( () => {
                //get response
                let httpResponse = JSON.parse(JSON.stringify(response));

                if(httpResponse.error){
                    this.responseMsg = httpResponse.error;
                    (document.querySelector('.error') as HTMLElement).style.display = 'block';
                    (document.querySelector('.alert-error') as HTMLElement).style.display = 'block';

                }else{
                    this.responseMsg = httpResponse.success;
                    (document.querySelector('.check_mark') as HTMLElement).style.display = 'block';
                    (document.querySelector('.alert-success') as HTMLElement).style.display = 'block';
                }

            }, 700 );

            },
            error => {
                (document.querySelector('.LoaderBalls') as HTMLElement).style.display = 'none';
                (document.querySelector('.error') as HTMLElement).style.display = 'block';
                console.log("Error", error);
            }
        );
        }, 700 );
    //this.activeModal.close(this.addNewBookForm.value);
  }
  ngOnInit() {
    this.addNewBikeForm = this.formBuilder.group({
        plateNumber: ['', [Validators.required,]],
        make: ['', Validators.required],
        model: ['', Validators.required],
        gears: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
        ABS: ['', [Validators.required]],
    });
}

get f() { return this.addNewBikeForm.controls; }


}
