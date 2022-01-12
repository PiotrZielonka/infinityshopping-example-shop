import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICategoryMinerals } from '../category-minerals.model';

export type EntityResponseType = HttpResponse<ICategoryMinerals>;
export type EntityArrayResponseType = HttpResponse<ICategoryMinerals[]>;


@Injectable({ providedIn: 'root' })
export class CategoryMineralsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/category-minerals');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICategoryMinerals>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICategoryMinerals[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(categoryMinerals: ICategoryMinerals): ICategoryMinerals {
    return Object.assign({}, categoryMinerals, {
      createTime: categoryMinerals.createTime?.isValid() ? categoryMinerals.createTime.toJSON() : undefined,
      updateTime: categoryMinerals.updateTime?.isValid() ? categoryMinerals.updateTime.toJSON() : undefined,
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
      res.body.forEach((categoryMinerals: ICategoryMinerals) => {
        categoryMinerals.createTime = categoryMinerals.createTime ? dayjs(categoryMinerals.createTime) : undefined;
        categoryMinerals.updateTime = categoryMinerals.updateTime ? dayjs(categoryMinerals.updateTime) : undefined;
      });
    }
    return res;
  }
}
