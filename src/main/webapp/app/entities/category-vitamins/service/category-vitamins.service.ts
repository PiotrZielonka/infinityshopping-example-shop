import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategoryVitamins } from '../category-vitamins.model';

export type EntityResponseType = HttpResponse<ICategoryVitamins>;
export type EntityArrayResponseType = HttpResponse<ICategoryVitamins[]>;


@Injectable({ providedIn: 'root' })
export class CategoryVitaminsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/category-vitamins');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategoryVitamins>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategoryVitamins[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(categoryVitamins: ICategoryVitamins): ICategoryVitamins {
    return Object.assign({}, categoryVitamins, {
      createTime: categoryVitamins.createTime?.isValid() ? categoryVitamins.createTime.toJSON() : undefined,
      updateTime: categoryVitamins.updateTime?.isValid() ? categoryVitamins.updateTime.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createTime = res.body.createTime ? dayjs(res.body.createTime) : undefined;
      res.body.updateTime = res.body.updateTime ? dayjs(res.body.updateTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((categoryVitamins: ICategoryVitamins) => {
        categoryVitamins.createTime = categoryVitamins.createTime ? dayjs(categoryVitamins.createTime) : undefined;
        categoryVitamins.updateTime = categoryVitamins.updateTime ? dayjs(categoryVitamins.updateTime) : undefined;
      });
    }
    return res;
  }
}
