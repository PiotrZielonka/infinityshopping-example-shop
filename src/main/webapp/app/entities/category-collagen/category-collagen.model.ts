import * as dayjs from 'dayjs';
import { ProductCategoryEnum } from 'app/entities/enumerations/product-category-enum.model';

export interface ICategoryCollagen {
  id?: number;
  productCategoryEnum?: ProductCategoryEnum;
  name?: string;
  quantity?: number | null;
  priceNet?: number;
  vat?: number;
  priceGross?: number | null;
  stock?: number;
  description?: string;
  createTime?: dayjs.Dayjs | null;
  updateTime?: dayjs.Dayjs | null;
  imageContentType?: string;
  image?: string;
}

export class CategoryCollagen implements ICategoryCollagen {
  constructor(
    public id?: number,
    public productCategoryEnum?: ProductCategoryEnum,
    public name?: string,
    public quantity?: number | null,
    public priceNet?: number,
    public vat?: number,
    public priceGross?: number | null,
    public stock?: number,
    public description?: string,
    public createTime?: dayjs.Dayjs | null,
    public updateTime?: dayjs.Dayjs | null,
    public imageContentType?: string,
    public image?: string
  ) {}
}

export function getCategoryCollagenIdentifier(categoryCollagen: ICategoryCollagen): number | undefined {
  return categoryCollagen.id;
}
