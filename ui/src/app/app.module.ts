import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { MatTableModule, MatSortModule} from '@angular/material';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import 'hammerjs';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { MenuComponent } from './menu/menu.component';
import { RouterModule, Routes} from '@angular/router';
import { MaterialModule} from './material.module';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { ReturnFormComponent} from './checkAvailability/form/form.component';
import { ListItemsComponent } from './list-items/list-items.component';
import { CarsComponent } from './list-items/cars/cars.component';
import { BikesComponent } from './list-items/bikes/bikes.component';
import { RegisterComponent } from './user/register/register.component';
import { AddCarComponent } from './list-items/cars/add/add.component';
import { AddBikeComponent } from './list-items/bikes/add/add.component';
import { DeleteComponent } from './delete/delete.component';
import { ReserveComponent } from './reserve/reserve.component';
import { SearchComponent } from './search/search.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuComponent,
    ListItemsComponent,
    CarsComponent,
    BikesComponent,
    RegisterComponent,
    ReturnFormComponent,
    AddCarComponent,
    AddBikeComponent,
    DeleteComponent,
    ReserveComponent,
    SearchComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    NgbModule,
    RouterModule.forRoot([
      {path: '', component: MenuComponent},
      {path: 'return', component: ReturnFormComponent},
      {path: 'list', component: ListItemsComponent},
      {path: 'list/cars', component: CarsComponent},
      {path: 'list/bikes', component: BikesComponent},
      {path: 'reserve', component: ReserveComponent},
      {path: 'delete', component: DeleteComponent},
      {path: 'search', component: SearchComponent}
    ])
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [
    AddCarComponent,
    AddBikeComponent
  ]
})
export class AppModule { }
