import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategoryVitamins, CategoryVitamins } from '../category-vitamins.model';
import { CategoryVitaminsService } from '../service/category-vitamins.service';

@Injectable({ providedIn: 'root' })
export class CategoryVitaminsRoutingResolveService implements Resolve<ICategoryVitamins> {
  constructor(protected service: CategoryVitaminsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategoryVitamins> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categoryVitamins: HttpResponse<CategoryVitamins>) => {
          if (categoryVitamins.body) {
            return of(categoryVitamins.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CategoryVitamins());
  }
}
