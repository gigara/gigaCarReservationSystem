import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormGroup, FormBuilder, FormArray, Validators} from '@angular/forms';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {backend_url} from '../../../global';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  styleUrls: ['./add.component.css']
})
export class AddCarComponent implements OnInit {
  addNewBookForm: FormGroup;
  responseMsg: String;

  constructor(public activeModal: NgbActiveModal, private formBuilder: FormBuilder, private http: HttpClient) {
  }

  //popup close button
  closeModal() {
    this.activeModal.close('Modal Closed');
  }

  //form submit button
  submitForm() {
    //post request body
    let body = new URLSearchParams();
    body.append('plateNo', this.addNewBookForm.value.plateNumber);
    body.append('make', this.addNewBookForm.value.make);
    body.append('model', this.addNewBookForm.value.model);
    body.append('noOfSeats', this.addNewBookForm.value.noOfSeats);
    body.append('withAC', this.addNewBookForm.value.withAC);
    body.append('transmission', this.addNewBookForm.value.transmission);
    body.append('type', "car");

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
    //init form
    this.addNewBookForm = this.formBuilder.group({
      plateNumber: ['', [Validators.required]],
      make: ['', Validators.required],
      model: ['', Validators.required],
      noOfSeats: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      AC: ['', [Validators.required]],
      transmission: ['', [Validators.required]]
    });
  }


  get f() {
    return this.addNewBookForm.controls;
  }


}
