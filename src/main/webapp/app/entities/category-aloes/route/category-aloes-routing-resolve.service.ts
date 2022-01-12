import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategoryAloes, CategoryAloes } from '../category-aloes.model';
import { CategoryAloesService } from '../service/category-aloes.service';

@Injectable({ providedIn: 'root' })
export class CategoryAloesRoutingResolveService implements Resolve<ICategoryAloes> {
  constructor(protected service: CategoryAloesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategoryAloes> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categoryAloes: HttpResponse<CategoryAloes>) => {
          if (categoryAloes.body) {
            return of(categoryAloes.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CategoryAloes());
  }
}
