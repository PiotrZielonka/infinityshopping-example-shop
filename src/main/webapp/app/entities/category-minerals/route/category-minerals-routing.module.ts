import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryMineralsComponent } from '../list/category-minerals.component';

const categoryMineralsRoute: Routes = [
  {
    path: '',
    component: CategoryMineralsComponent,
    data: {
      authorities: [],
      defaultSort: 'id,asc',
      pageTitle: 'infinityshoppingApp.categoryMinerals.home.title'
    },
    canActivate: [],
  }
];

@NgModule({
  imports: [RouterModule.forChild(categoryMineralsRoute)],
  exports: [RouterModule],
})
export class CategoryMineralsRoutingModule {}