package com.app.album_maker.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("avatar_path")
        var avatarPath: String? = null,
        @SerializedName("avatar_url")
        var avatarUrl: String? = null,
        @SerializedName("department_title")
        var departmentTitle: String? = null,
        @SerializedName("department_id")
        var departmentId: String? = null,
        @SerializedName("department")
        var department: String? = null,
        @SerializedName("full_name")
        var fullName: String? = null,
        @SerializedName("position_title")
        var positionTitle: String? = null,
        @SerializedName("position_id")
        var positionId: String? = null,
        @SerializedName("position")
        var position: String? = null,
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("address")
        var address: String? = "",
        @SerializedName("staff_code")
        var staffCode: String? = ""
) : Parcelable