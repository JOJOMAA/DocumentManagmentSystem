import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-pdf-upload',
  standalone: true,
  imports: [MatButtonModule,
  MatIconModule],
  templateUrl: './pdf-upload.component.html',
  styleUrl: './pdf-upload.component.css'
})

//https://blog.angular-university.io/angular-file-upload/
//ToDO: Upload Bestätigung anzeigen
export class PdfUploadComponent {
  fileName = '';
  file:File = null;
  constructor(private http: HttpClient){}

  selectPdf(event){
     this.file = event.target.files[0];
     this.fileName = this.file.name;
  }

  uploadPdf(file){
    if(file){
      const formData = new FormData();

      formData.append("file", file);

      formData.append("name", file.name);
      const upload$ = this.http.post("http://localhost:8081/documents/upload", formData);
      upload$.subscribe();
      this.resetFile()
    }
  }

  resetFile(){
    this.file = null;
    this.fileName = '';
  }
}
