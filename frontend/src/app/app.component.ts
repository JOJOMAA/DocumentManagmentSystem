import { Component, Renderer2} from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PdfUploadComponent } from "./pdf-upload/pdf-upload.component";
import { PdfListComponent } from "./pdf-list/pdf-list.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PdfUploadComponent, PdfListComponent, MatIconModule, MatButtonModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PaperlessFrontend';
  isDarkMode = false;

  constructor(private renderer: Renderer2) {}

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    const theme = this.isDarkMode ? 'dark' : 'light';
    if (this.isDarkMode) {
      this.renderer.setAttribute(document.body, 'data-theme', 'dark');
    } else {
      this.renderer.removeAttribute(document.body, 'data-theme');
    }
  }
}
