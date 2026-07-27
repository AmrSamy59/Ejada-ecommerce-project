import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { toast } from 'ngx-sonner';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, PaginationComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  products: any[] = [];
  isLoading = true;
  addingToCart: number | null = null;

  // Pagination state
  currentPage = 0;
  pageSize = 4;
  totalPages = 0;
  totalElements = 0;

  private productService = inject(ProductService);
  private cartService = inject(CartService);

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(page: number = 0): void {
    this.isLoading = true;
    this.currentPage = page;
    
    this.productService.getProducts(this.currentPage, this.pageSize).subscribe({
      next: (res) => {
        this.products = res.content || res;
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElements || 0;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load products', err);
        this.isLoading = false;
      }
    });
  }

  addToCart(product: any) {
    this.addingToCart = product.id;
    this.cartService.addToCart(product);
    toast.success(`${product.name} added to cart!`);
    this.addingToCart = null;
  }
}
