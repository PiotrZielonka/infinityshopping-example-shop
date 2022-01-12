import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CategoryCollagenComponent } from './list/category-collagen.component';
import { CategoryCollagenRoutingModule } from './route/category-collagen-routing.module';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

@NgModule({
  imports: [
    SharedModule,
    CategoryCollagenRoutingModule,
    Ng2SearchPipeModule
  ],
  declarations: [CategoryCollagenComponent],
})
export class CategoryCollagenModule {}
