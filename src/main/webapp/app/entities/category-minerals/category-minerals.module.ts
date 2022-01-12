import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CategoryMineralsComponent } from './list/category-minerals.component';
import { CategoryMineralsRoutingModule } from './route/category-minerals-routing.module';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

@NgModule({
  imports: [
    SharedModule,
    CategoryMineralsRoutingModule,
    Ng2SearchPipeModule
  ],
  declarations: [CategoryMineralsComponent],
})
export class CategoryMineralsModule {}
