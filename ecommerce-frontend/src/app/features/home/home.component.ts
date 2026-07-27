import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { toast } from 'ngx-sonner';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  products: any[] = [];
  isLoading = true;
  addingToCart: number | null = null;

  private productService = inject(ProductService);
  private cartService = inject(CartService);

  ngOnInit(): void {
    this.productService.getProducts(0, 50).subscribe({
      next: (res) => {
        this.products = res.content || res;
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
