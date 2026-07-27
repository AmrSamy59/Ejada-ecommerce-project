import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface CartItem {
  product: any;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.loadCart());
  public cartItems$ = this.cartItemsSubject.asObservable();

  constructor() {}

  private loadCart(): CartItem[] {
    const saved = localStorage.getItem('cart');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        return [];
      }
    }
    return [];
  }

  private saveCart(items: CartItem[]): void {
    localStorage.setItem('cart', JSON.stringify(items));
    this.cartItemsSubject.next(items);
  }

  addToCart(product: any, quantity: number = 1): void {
    const currentCart = this.cartItemsSubject.value;
    const existing = currentCart.find(item => item.product.id === product.id);

    if (existing) {
      existing.quantity += quantity;
      this.saveCart([...currentCart]);
    } else {
      this.saveCart([...currentCart, { product, quantity }]);
    }
  }

  removeFromCart(productId: number): void {
    const currentCart = this.cartItemsSubject.value;
    const updatedCart = currentCart.filter(item => item.product.id !== productId);
    this.saveCart(updatedCart);
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    const currentCart = this.cartItemsSubject.value;
    const existing = currentCart.find(item => item.product.id === productId);
    if (existing) {
      existing.quantity = quantity;
      this.saveCart([...currentCart]);
    }
  }

  clearCart(): void {
    this.saveCart([]);
  }

  getCartTotal(): number {
    return this.cartItemsSubject.value.reduce((total, item) => total + (item.product.price * item.quantity), 0);
  }

  getCartItemCount(): number {
    return this.cartItemsSubject.value.reduce((count, item) => count + item.quantity, 0);
  }
}
