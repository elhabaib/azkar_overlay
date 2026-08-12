<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="24dp"
    android:background="#0B3D3A">

    <ImageView
        android:layout_width="140dp"
        android:layout_height="140dp"
        android:src="@drawable/logo"
        android:layout_marginBottom="24dp"
        android:contentDescription="@string/app_name"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textColor="#F3EDE0"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="8dp"/>

    <TextView
        android:id="@+id/statusText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="الخدمة متوقفة"
        android:textColor="#C9A227"
        android:layout_marginBottom="32dp"/>

    <Button
        android:id="@+id/permissionBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="١. فعّل صلاحية الظهور فوق التطبيقات"
        android:layout_marginBottom="12dp"/>

    <Button
        android:id="@+id/startBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="٢. ابدأ التذكير"
        android:layout_marginBottom="12dp"/>

    <Button
        android:id="@+id/stopBtn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="إيقاف التذكير"/>

</LinearLayout>
