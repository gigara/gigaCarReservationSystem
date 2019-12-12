import { Component, OnInit } from '@angular/core';
import { MatSort, MatSortable, MatTableDataSource } from '@angular/material';
import { HttpClient } from '@angular/common/http';
import { backend_url } from '../global';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  dataSource;
  displayedColumns = ['isbn','title','sector']; //table headers
  
  constructor(private http: HttpClient) { }

  ngOnInit() {
    let title = prompt("Please enter the title to search:");
    this.http.get(backend_url+'/item/search/'+title.toLowerCase()).subscribe(results => {
      if(!results){
        return;
      }
      //this.dataSource = new MatTableDataSource(results["DVDs"]); // set datasource
      this.dataSource = results;
    }) 
  }

}
