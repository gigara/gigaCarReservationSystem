import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort, MatSortable, MatTableDataSource } from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddCarComponent } from './add/add.component';
import { backend_url } from '../../global';
import {FormControl} from "@angular/forms";
import {makeAnimationEvent} from "@angular/animations/browser/src/render/shared";

@Component({
  selector: 'app-cars',
  templateUrl: './cars.component.html',
  styleUrls: ['./cars.component.css']
})
export class CarsComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;

  dataSource;
  displayedColumns = ['plateNumber','make','model','noOfSeats','withAC','transmission', 'availability']; //table headers

  makeFilter = new FormControl('');
  modelFilter = new FormControl('');
  filterValues = {
    make: '',
    model: '',
  };

  constructor(private http: HttpClient,private modalService: NgbModal) { }


  openFormModal() {
    const modalRef = this.modalService.open(AddCarComponent);

    modalRef.result.then((result) => {
      console.log(result);
    }).catch((error) => {
      console.log(error);
    });
  }

  getCars(): Observable<Car[]>{
    // get data from the backend
    return this.http.get<Car[]>(backend_url+"/item/list");
  }

  getReserve(plateNo : string, day: string, month: string, year: string): Observable<string[]>{
    // get data from the backend
    return this.http.get<string[]>(backend_url+"/item/isAvailable/bool/" +plateNo +"/" + day +"/" + month +"/" + year +"/" + day +"/" + month +"/" + year);
  }

  ngOnInit() {
    //get data from the backend
    this.getCars().subscribe(results => {
      if(!results){
        return;
      }
      this.dataSource = new MatTableDataSource(results["Cars"]);  // set datasource
      this.dataSource.sort = this.sort;
      this.dataSource.filterPredicate = this.createFilter();
    });

    // filter
    this.makeFilter.valueChanges
      .subscribe(
        make => {
          this.filterValues.make = make;
          this.dataSource.filter = JSON.stringify(this.filterValues);
        }
      );
    this.modelFilter.valueChanges
      .subscribe(
        model => {
          this.filterValues.model = model;
          this.dataSource.filter = JSON.stringify(this.filterValues);
        }
      )
  }

  //search item
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  //check availability
  runed: boolean = false;
  availability: boolean = false;
  today = new Date();
  day = String(this.today.getDate()).padStart(2, '0');
  month = String(this.today.getMonth() + 1).padStart(2, '0');
  year = String(this.today.getFullYear());
  isAvailable(plateNumer: any) {
    if (!this.runed) {
    this.getReserve(plateNumer, this.day, this.month, this.year).subscribe(results => {
      if (!results) {
        return;
      }
      this.runed = true;
      if (results.toString() == "true") {
        this.availability = true;
      }
    });
  }
    return this.availability;
  }

  createFilter(): (data: any, filter: string) => boolean {
    let filterFunction = function(data, filter): boolean {
      let searchTerms = JSON.parse(filter);
      return data.make.toLowerCase().indexOf(searchTerms.make) !== -1
        && data.model.toLowerCase().indexOf(searchTerms.model) !== -1;
    };
    return filterFunction;
  }
}
