import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {PdfUploadComponent} from "./pdf-upload/pdf-upload.component";
import {PdfListComponent} from "./pdf-list/pdf-list.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PdfUploadComponent, PdfListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PaperlessFrontend';
}
