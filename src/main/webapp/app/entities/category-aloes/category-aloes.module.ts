import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { CategoryAloesComponent } from './list/category-aloes.component';
import { CategoryAloesRoutingModule } from './route/category-aloes-routing.module';

@NgModule({
  imports: [
    SharedModule,
    CategoryAloesRoutingModule,
    Ng2SearchPipeModule
  ],
  declarations: [CategoryAloesComponent],
})
export class CategoryAloesModule {}
