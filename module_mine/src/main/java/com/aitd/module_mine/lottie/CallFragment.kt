package com.aitd.module_mine.lottie

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.aitd.library_common.base.BaseFragment
import com.aitd.module_mine.R
import com.aitd.module_mine.sliding.*
import com.aitd.module_mine.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_call.*

class CallFragment : BaseFragment() {

    private var mNavigationFragment: NavFragment? = null
    private var mBussinessCardFragment: BusFragment? = null
    private var mShopsFragment: ShopFragment? = null
    private var mProjectFragment: ProjectFragment? = null
    private var mWebPageFragment: WebFragment? = null

    private var src = arrayOf("商铺", "项目", "名片", "导航", "网页")
    private val order = intArrayOf(R.mipmap.roulette_shop, R.mipmap.roulette_project, R.mipmap.roulette_business_card, R.mipmap.roulette_daohang, R.mipmap.roulette_webpage)

    override fun init(saveInstanceState: Bundle?) {
        ScreenUtils.initScreen(mActivity)  // 自定义view   163行 Bitmap.createBitmap  需要初始化宽高

//       private List<Daohang> mydaohang = new ArrayList<>();
//        src = arrayOfNulls(daohangs.size)
//        imgs = IntArray(daohangs.size)     也可通过后台获取数组
//        Collections.sort(daohangs)
//        mydaohang.clear()
//        mydaohang.addAll(daohangs)
//        rankLength = daohangs.get(daohangs.size - 1).getRank()
//        //再装数据
//        //再装数据
//        for (i in daohangs.indices) {
//            src[i] = daohangs.get(i).getName()
//            if (daohangs.get(i).getRank() > 5) {
//                imgs.get(i) = order[0]
//            } else {
//                imgs.get(i) = order[daohangs.get(i).getRank() - 1]
//            }
//        }

        sliding_arc_view.SetSrcAndTitles(order, src)  // 自定义view需要填充数据，现在是写死
        setSelection(2)//默认选择第几个tab
        sliding_arc_view.ChooseIndex(2)  //默认选择第几个tab

        sliding_arc_view.setQtScrollListener { v, index -> setSelection(index) }
        sliding_arc_view.setQtItemClickListener { v, index -> setSelection(index) }
    }



    /*
        private void initRecycler() {
        adapter = new ProjectMultAdapter(this, new ProjectMultAdapter.OnPictureClickListener() {
            @Override
            public void imgClick(int position) {
                delOption();
                adapter.getDataList().remove(position);
                adapter.notifyDataSetChanged();
                addOption();
            }
        });
        adapter.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        addOption();
        int spacing = getResources().getDimensionPixelSize(R.dimen.dp10);
        recyclerView.addItemDecoration(new MediaGridInset(3, spacing, false));
        recyclerView.setAdapter(adapter);
    }


    private void addOption() {
        if (adapter.getDatasSize() > 0) {
            if (adapter.getDatasSize() < 6) {
                Project lastItem = adapter.getDataList().get(adapter.getDatasSize() - 1);
                if (lastItem.getAdditionsDeletions() != 1) {
                    Project addItem = new Project();
                    addItem.setAdditionsDeletions(1);
                    adapter.appenData(addItem);
                }
            }
        } else {
            Project addItem = new Project();
            addItem.setAdditionsDeletions(1);
            adapter.appenData(addItem);
        }
    }

    private void delOption() {
        if (adapter.getDatasSize() > 0) {
            Project lastItem = adapter.getDataList().get(adapter.getDatasSize() - 1);
            if (lastItem.getAdditionsDeletions() == 1) {
                adapter.removeData(adapter.getDatasSize() - 1);
            }
        }
    }
 @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            //此处是不选图片，按了返回键保证视图还在
            if (requestCode == REQUEST_CODE_CHOOSE) {
                addOption();
            }
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_SINGLE:
                if (data != null) {
                    List<Uri> pathList = Matisse.obtainResult(data);
                    Uri uri = pathList.get(0);
                    upImage(getRealFilePath(uri));
                }
                break;
            case TAKE_PICTURE:
                delOption();
                Project newData = new Project();
                newData.setPicUrl(path);
                newData.setAdditionsDeletions(0);
                adapter.appenData(newData);
                addOption();
                break;
            case REQUEST_CODE_CHOOSE:
                List<Project> list = convertObj(Matisse.obtainPathResult(data));
                adapter.setLoadMoreData(list);
                addOption();
                break;
            case REQEUST_CODE:
                Bundle extras = data.getExtras();
                if (extras != null) {
                    ArrayList<Project> temList = extras.getParcelableArrayList(ImageOptionActivity.DATA);
                    adapter.setNewData(temList);
                }
                addOption();
                break;
        }
    }
        /**
     * 调用图库选择
     */
    private void callGallery() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.thunder.sample.fileprovider"))
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                    }
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {
                    }
                })
                .forResult(REQUEST_CODE_CHOOSE_SINGLE);
    }


    //拍照
    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        StringBuffer sDir = new StringBuffer();
        if (hasSDcard()) {
            sDir.append(Environment.getExternalStorageDirectory() + "/DCIM/");
        } else {
            String dataPath = Environment.getRootDirectory().getPath();
            sDir.append(dataPath + "/DCIM/");
        }

        File fileDir = new File(sDir.toString());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        picfile = new File(fileDir, String.valueOf(System.currentTimeMillis()) + ".jpg");
        path = picfile.getPath();
        imageUri = Uri.fromFile(picfile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        openCameraIntent.putExtra("return-data", false);
        openCameraIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        openCameraIntent.putExtra("noFaceDetection", true);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    private void selectLocalImage() {
        delOption();
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(false)
                .maxSelectable(Math.abs(adapter.getDatasSize() - 6))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener(new OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {
                    }
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(new OnCheckedListener() {
                    @Override
                    public void onCheck(boolean isChecked) {
                    }
                })
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onItemClick(RecyclerViewHolder viewHolder, Project data, int position) {
        if (data.getAdditionsDeletions() == 0) {
            delOption();
            adapter.getDataList().remove(position);
            adapter.notifyDataSetChanged();
            addOption();
        } else {
            showPhotoDialog();
        }
    }

     */



    private fun setSelection(index: Int) {
        val transaction = fragmentManager?.beginTransaction()
        hideAllFragments(transaction!!)
        when (index) {
            0 -> {
                if (mShopsFragment == null) {
                    mShopsFragment = ShopFragment()
                    transaction.add(R.id.fragment_container, mShopsFragment!!)
                } else {
                    transaction.show(mShopsFragment!!)
                }
            }
            1 -> {
                if (mProjectFragment == null) {
                    mProjectFragment = ProjectFragment()
                    transaction.add(R.id.fragment_container, mProjectFragment!!)
                } else {
                    transaction.show(mProjectFragment!!)
                }
            }
            2 -> {
                if (mBussinessCardFragment == null) {
                    mBussinessCardFragment = BusFragment()
                    transaction.add(R.id.fragment_container, mBussinessCardFragment!!)
                } else {
                    transaction.show(mBussinessCardFragment!!)
                }
            }
            3 -> {
                if (mNavigationFragment == null) {
                    mNavigationFragment = NavFragment()
                    transaction.add(R.id.fragment_container, mNavigationFragment!!)
                } else {
                    transaction.show(mNavigationFragment!!)
                }
            }
            4 -> {
                if (mWebPageFragment == null) {
                    mWebPageFragment = WebFragment()
                    transaction.add(R.id.fragment_container, mWebPageFragment!!)
                } else {
                    transaction.show(mWebPageFragment!!)
                }
            }
        }
        transaction.commit()
    }

    private fun hideAllFragments(fragmentTransaction: FragmentTransaction) {
        if (mNavigationFragment != null) {
            fragmentTransaction.hide(mNavigationFragment!!)
        }
        if (mBussinessCardFragment != null) {
            fragmentTransaction.hide(mBussinessCardFragment!!)
        }
        if (mShopsFragment != null) {
            fragmentTransaction.hide(mShopsFragment!!)
        }
        if (mWebPageFragment != null) {
            fragmentTransaction.hide(mWebPageFragment!!)
        }
        if (mProjectFragment != null) {
            fragmentTransaction.hide(mProjectFragment!!)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_call
}