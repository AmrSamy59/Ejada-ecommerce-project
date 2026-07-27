import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService, CartItem } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  isCheckingOut = false;
  
  private cartService = inject(CartService);
  private orderService = inject(OrderService);
  private router = inject(Router);

  ngOnInit(): void {
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });
  }

  get total(): number {
    return this.cartService.getCartTotal();
  }

  updateQuantity(productId: number, quantity: number): void {
    this.cartService.updateQuantity(productId, quantity);
  }

  removeItem(productId: number): void {
    this.cartService.removeFromCart(productId);
  }

  checkout(): void {
    if (this.cartItems.length === 0) return;
    
    this.isCheckingOut = true;
    const request = {
      items: this.cartItems.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      }))
    };

    this.orderService.placeOrder(request).subscribe({
      next: () => {
        this.cartService.clearCart();
        alert('Order placed successfully!');
        this.isCheckingOut = false;
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        console.error('Checkout failed', err);
        alert('Failed to place order. Please try again.');
        this.isCheckingOut = false;
      }
    });
  }
}
