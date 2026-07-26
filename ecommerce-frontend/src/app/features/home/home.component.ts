import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService, Product } from '../../core/services/product.service';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  addingToCart: number | null = null;
  
  private productService = inject(ProductService);
  private orderService = inject(OrderService);

  ngOnInit(): void {
    this.productService.getProducts(0, 20).subscribe({
      next: (res) => {
        this.products = res.content;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load products', err);
        this.isLoading = false;
      }
    });
  }

  addToCart(product: Product) {
    if (!localStorage.getItem('jwt_token')) {
      alert('Please login first to add items to cart.');
      return;
    }
    
    this.addingToCart = product.id;
    this.orderService.placeOrder({ items: [{ productId: product.id, quantity: 1 }] }).subscribe({
      next: () => {
        alert(`Order for ${product.name} placed successfully!`);
        this.addingToCart = null;
      },
      error: (err) => {
        alert('Failed to place order. You might be out of stock or need to login again.');
        this.addingToCart = null;
      }
    });
  }
}
