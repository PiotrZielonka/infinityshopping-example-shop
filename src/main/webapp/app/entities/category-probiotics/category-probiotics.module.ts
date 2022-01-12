import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CategoryProbioticsComponent } from './list/category-probiotics.component';
import { CategoryProbioticsRoutingModule } from './route/category-probiotics-routing.module';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

@NgModule({
  imports: [
    SharedModule,
    CategoryProbioticsRoutingModule,
    Ng2SearchPipeModule
  ],
  declarations: [CategoryProbioticsComponent],
})
export class CategoryProbioticsModule {}
