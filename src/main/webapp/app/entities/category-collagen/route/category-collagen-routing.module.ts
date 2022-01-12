import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryCollagenComponent } from '../list/category-collagen.component';


const categoryCollagenRoute: Routes = [
  {
    path: '',
    component: CategoryCollagenComponent,
    data: {
      authorities: [],
      defaultSort: 'id,asc',
      pageTitle: 'infinityshoppingApp.categoryCollagen.home.title'
    },
    canActivate: [],
  }
];

@NgModule({
  imports: [RouterModule.forChild(categoryCollagenRoute)],
  exports: [RouterModule],
})
export class CategoryCollagenRoutingModule {}