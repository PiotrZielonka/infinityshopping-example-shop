import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryVitaminsComponent } from '../list/category-vitamins.component';

const categoryVitaminsRoute: Routes = [
  {
    path: '',
    component: CategoryVitaminsComponent,
    data: {
      authorities: [],
      defaultSort: 'id,asc',
      pageTitle: 'infinityshoppingApp.categoryVitamins.home.title'
    },
    canActivate: [],
  }
];

@NgModule({
  imports: [RouterModule.forChild(categoryVitaminsRoute)],
  exports: [RouterModule],
})
export class CategoryVitaminsRoutingModule {}
