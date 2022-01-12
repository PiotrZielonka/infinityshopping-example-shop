import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryProbioticsComponent } from '../list/category-probiotics.component';

const categoryProbioticsRoute: Routes = [
  {
    path: '',
    component: CategoryProbioticsComponent,
    data: {
      authorities: [],
      defaultSort: 'id,asc',
      pageTitle: 'infinityshoppingApp.categoryProbiotics.home.title'
    },
    canActivate: [],
  }
];

@NgModule({
  imports: [RouterModule.forChild(categoryProbioticsRoute)],
  exports: [RouterModule],
})
export class CategoryProbioticsRoutingModule {}