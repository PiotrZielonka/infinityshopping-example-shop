import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryAloesComponent } from '../list/category-aloes.component';

const categoryAloesRoute: Routes = [
  {
    path: '',
    component: CategoryAloesComponent,
    data: {
      authorities: [],
      defaultSort: 'id,asc',
      pageTitle: 'infinityshoppingApp.categoryAloes.home.title'
    },
    canActivate: [],
  }
];

@NgModule({
  imports: [RouterModule.forChild(categoryAloesRoute)],
  exports: [RouterModule],
})
export class CategoryAloesRoutingModule {}
