import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

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
  private authService = inject(AuthService);
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
    if (!this.authService.isLoggedIn()) {
      alert('Please login to add items to your cart.');
      return;
    }
    
    this.addingToCart = product.id;
    this.cartService.addToCart(product);
    
    // Simulate slight delay for UI feedback
    setTimeout(() => {
      this.addingToCart = null;
    }, 500);
  }
}
