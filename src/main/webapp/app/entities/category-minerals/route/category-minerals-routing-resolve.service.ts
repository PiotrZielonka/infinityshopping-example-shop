import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategoryMinerals, CategoryMinerals } from '../category-minerals.model';
import { CategoryMineralsService } from '../service/category-minerals.service';

@Injectable({ providedIn: 'root' })
export class CategoryMineralsRoutingResolveService implements Resolve<ICategoryMinerals> {
  constructor(protected service: CategoryMineralsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategoryMinerals> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categoryMinerals: HttpResponse<CategoryMinerals>) => {
          if (categoryMinerals.body) {
            return of(categoryMinerals.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CategoryMinerals());
  }
}
