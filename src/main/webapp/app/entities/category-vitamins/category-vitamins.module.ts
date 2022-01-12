import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CategoryVitaminsComponent } from './list/category-vitamins.component';
import { CategoryVitaminsRoutingModule } from './route/category-vitamins-routing.module';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

@NgModule({
  imports: [
    SharedModule,
    CategoryVitaminsRoutingModule,
    Ng2SearchPipeModule

  ],
  declarations: [CategoryVitaminsComponent],
})
export class CategoryVitaminsModule {}
