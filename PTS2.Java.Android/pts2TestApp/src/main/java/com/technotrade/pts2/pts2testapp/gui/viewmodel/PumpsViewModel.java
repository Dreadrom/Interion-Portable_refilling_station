package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.chooseAmountWithKeyboard;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.chooseNozzleWithList;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.chooseVolumeWithKeyboard;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.navigateToPump;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.navigateUp;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showNozzleIsNotPickedUpDialog;
import static com.technotrade.pts2.pts2testapp.gui.viewmodel.ViewModelCommand.showOrderConfirmation;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.technotrade.pts2.datastructs.FuelGrade;
import com.technotrade.pts2.datastructs.MeasurementUnits;
import com.technotrade.pts2.datastructs.Pump;
import com.technotrade.pts2.datastructs.PumpDisplayData;
import com.technotrade.pts2.datastructs.PumpEndOfTransactionStatus;
import com.technotrade.pts2.datastructs.PumpFillingStatus;
import com.technotrade.pts2.datastructs.PumpIdleStatus;
import com.technotrade.pts2.datastructs.PumpNozzles;
import com.technotrade.pts2.datastructs.PumpOfflineStatus;
import com.technotrade.pts2.datastructs.PumpTotals;
import com.technotrade.pts2.datastructs.PumpsConfiguration;
import com.technotrade.pts2.pts2testapp.ApplicationFacade;
import com.technotrade.pts2.pts2testapp.BR;
import com.technotrade.pts2.pts2testapp.DataStorage;
import com.technotrade.pts2.pts2testapp.OrderManager;
import com.technotrade.pts2.pts2testapp.PTSManager;
import com.technotrade.pts2.pts2testapp.adapter.NozzlesRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.adapter.PumpsRecyclerViewAdapter;
import com.technotrade.pts2.pts2testapp.entity.DataHolder;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;
import com.technotrade.pts2.pts2testapp.entity.NozzleItem;
import com.technotrade.pts2.pts2testapp.entity.PumpItem;
import com.technotrade.pts2.pts2testapp.listener.OnPumpItemClickListener;
import com.technotrade.pts2.pts2testapp.statemachine.StateData;
import com.technotrade.pts2.pts2testapp.statemachine.StateMachine;
import com.technotrade.pts2.pts2testapp.statemachine.states.AuthorizingState;
import com.technotrade.pts2.pts2testapp.statemachine.states.CurrencySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.FuelingState;
import com.technotrade.pts2.pts2testapp.statemachine.states.IdleState;
import com.technotrade.pts2.pts2testapp.statemachine.states.NozzleSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpNotSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.PumpSelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.QuantitySelectedState;
import com.technotrade.pts2.pts2testapp.statemachine.states.StoppingState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PumpsViewModel extends BaseViewModel {
    public static class ObservableFieldsForTwoWayBinding extends BaseObservable {
        private final MutableLiveData<PumpItem> mSelectedPump;
        private final MutableLiveData<NozzleItem> mSelectedNozzle;

        public ObservableFieldsForTwoWayBinding() {
            mSelectedPump = new MutableLiveData<>();
            mSelectedNozzle = new MutableLiveData<>();
        }

        @Bindable
        public MutableLiveData<PumpItem> getSelectedPump() {
            return mSelectedPump;
        }

        public void setSelectedPump(PumpItem pumpItem) {
            mSelectedPump.postValue(pumpItem);
            notifyPropertyChanged(BR.selectedPump);
        }

        @Bindable
        public MutableLiveData<NozzleItem> getSelectedNozzle() {
            return mSelectedNozzle;
        }

        public void setSelectedNozzle(NozzleItem nozzleItem) {
            mSelectedNozzle.postValue(nozzleItem);
            notifyPropertyChanged(BR.selectedNozzle);
        }
    }

    private final ObservableFieldsForTwoWayBinding mTwoWayFields;

    public ObservableFieldsForTwoWayBinding getTwoWayFields() {
        return mTwoWayFields;
    }

    private final PTSManager mPTSManager;
    private final DataStorage mDataStorage;
    private final StateMachine mStateMachine;
    private final MutableLiveData<List<PumpItem>> mPumps;
    private final MutableLiveData<List<NozzleItem>> mNozzles;
    private final PumpsRecyclerViewAdapter mPumpsRecyclerViewAdapter;
    private final NozzlesRecyclerViewAdapter mNozzlesRecyclerViewAdapter;
    private final MutableLiveData<Boolean> mDataInitialized = new MutableLiveData<>(false);
    private boolean mPumpsInitialized = false;
    private boolean mNozzlesInitialized = false;
    private boolean mAnyPumpStatusInitialized = false;

    public LiveData<Boolean> getDataInitialized() {
        return mDataInitialized;
    }

    final String predefinedVolumeDefault = String.valueOf(ApplicationFacade.getInstance().getSettings().getPredefinedVolumeInt());
    public final ObservableField<String> predefinedVolume = new ObservableField<>(predefinedVolumeDefault);

    final String predefinedAmountDefault = ApplicationFacade.getInstance().getSettings().getPredefinedAmount().toString();
    public final ObservableField<String> predefinedAmount = new ObservableField<>(predefinedAmountDefault);

    public final ObservableField<String> currency = new ObservableField<>(ApplicationFacade.getInstance().getSettings().getCurrency());
    public final ObservableField<String> volumeUnits = new ObservableField<>("");

    public LiveData<String> getOrderValue() {
        final OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
        MutableLiveData<String> liveData = new MutableLiveData<>();
        liveData.setValue(orderManager.getOrderValue());
        return liveData;
    }

    public LiveData<String> getOrderUnit() {
        final OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();
        MutableLiveData<String> liveData = new MutableLiveData<>();
        liveData.setValue(orderManager.getOrderUnit());
        return liveData;
    }

    public PumpsViewModel() {
        mTwoWayFields = new ObservableFieldsForTwoWayBinding();
        mPTSManager = ApplicationFacade.getInstance().getPTSManager();
        mDataStorage = mPTSManager.getDataStorage();
        mStateMachine = ApplicationFacade.getInstance().getStateMachine();
        mPumps = new MutableLiveData<>();
        mNozzles = new MutableLiveData<>();
        mPumpsRecyclerViewAdapter = new PumpsRecyclerViewAdapter();
        mNozzlesRecyclerViewAdapter = new NozzlesRecyclerViewAdapter();

        initOnClickListeners();
        initFieldsObserver();
        initOpenedListener();
        initPumpsConfigurationListener();
        initNozzlesConfigurationListener();
        initPumpIdleStatusDataListener();
        initPumpFillingStatusDataListener();
        initPumpEndOfTransactionStatusListener();
        initPumpOfflineStatusListener();
        initPumpTotalsListener();
        initPumpDisplayDataListener();
        initMeasurementUnitsListener();
    }

    private void initOnClickListeners() {
        mPumpsRecyclerViewAdapter.addOnPumpItemClickListener(new OnPumpItemClickListener() {
            @Override
            public void onPumpItemClick(PumpItem item) {
                onRequestToNavigateToPump(item);
            }
        });
    }

    private void initFieldsObserver() {

        final OrderManager orderManager = ApplicationFacade.getInstance().getOrderManager();

        mTwoWayFields.getSelectedPump().observeForever(newSelectedPump -> {
            if (newSelectedPump != null) {
                if(newSelectedPump.getNozzle() == 0) {
                    mTwoWayFields.setSelectedNozzle(null);
                }

                mDataStorage.setSelectedPump(newSelectedPump);
            }
        });

        mTwoWayFields.getSelectedNozzle().observeForever(newSelectedNozzle -> {
            if (newSelectedNozzle != null) {
                mDataStorage.setSelectedNozzle(newSelectedNozzle);
            }
        });

        mPumps.observeForever(newPumps -> {
            if (newPumps != null) {
                final PumpItem selectedPump = getSelectedPump().getValue();

                if(selectedPump != null) {
                    Optional<PumpItem> foundPump = newPumps.stream()
                        .filter(item -> item.getNumber() == selectedPump.getNumber())
                        .findFirst();

                    foundPump.ifPresent(this::setSelectedPump);
                }

                orderManager.updateOrdersProgressIndicators(newPumps);
            }
        });
    }

    private void initOpenedListener() {
        DataHolder<Boolean> connectedDataHolder = mDataStorage.getOpenedDataHolder();
        connectedDataHolder.addOnDataChangeListener(opened -> {
            if (!opened) {
                resetInitializationDataReady();
            }
        });
    }

    private void initPumpsConfigurationListener() {
        DataHolder<PumpsConfiguration> pumpsConfigurationDataHolder = mDataStorage.getPumpsConfigurationDataHolder();
        pumpsConfigurationDataHolder.addOnDataChangeListener(data -> {
            List<Pump> pumps = data.getPumps();
            List<PumpItem> pumpItems = new ArrayList<>();

            for (Pump pump : pumps) {
                PumpItem pumpItem = new PumpItem();
                pumpItem.setNumber(pump.getId());
                pumpItems.add(pumpItem);
            }

            setPumps(pumpItems);
            mPumpsInitialized = true;
            checkInitializationDataReady();
        });
    }

    private void initNozzlesConfigurationListener() {
        DataHolder<ArrayList<PumpNozzles>> pumpNozzlesConfigurationDataHolder = mDataStorage.getPumpNozzlesConfigurationDataHolder();
        pumpNozzlesConfigurationDataHolder.addOnDataChangeListener(data -> {
            mNozzlesInitialized = true;
            checkInitializationDataReady();
        });
    }

    private void initPumpIdleStatusDataListener() {
        DataHolder<PumpIdleStatus> pumpIdleStatusDataHolder = mDataStorage.getPumpIdleStatusDataHolder();
        pumpIdleStatusDataHolder.addOnDataChangeListener(pumpIdleStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpIdleStatus.getPump()) {
                        item.setStateDescription(pumpIdleStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpIdleStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpIdleStatus.getStatus()));
                        item.setNozzle(pumpIdleStatus.getNozzle());
                        item.setFuelName(pumpIdleStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpIdleStatus.getLastPrice()));
                        item.setVolume(String.valueOf(pumpIdleStatus.getLastVolume()));
                        item.setAmount(String.valueOf(pumpIdleStatus.getLastAmount()));

                        // Data of current refuelling - used for calculating progress-bars
                        item.setDispensedVolume("0");
                        item.setDispensedAmount("0");

                        StateData stateData = new StateData();
                        stateData.setText(String.valueOf(item.getNozzle()));
                        stateData.setViewModel(this);
                        stateData.setPumpItem(item);

                        mStateMachine.transition(new IdleState(), stateData);

                        // Set pump status later than other data because previous status can be used in state transition
                        item.setStatus(pumpIdleStatus.getStatus());

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initPumpFillingStatusDataListener() {
        DataHolder<PumpFillingStatus> pumpFillingStatusDataHolder = mDataStorage.getPumpFillingStatusDataHolder();
        pumpFillingStatusDataHolder.addOnDataChangeListener(pumpFillingStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpFillingStatus.getPump()) {
                        item.setStateDescription(pumpFillingStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpFillingStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpFillingStatus.getStatus()));
                        item.setNozzle(pumpFillingStatus.getNozzle());
                        item.setFuelName(pumpFillingStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpFillingStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpFillingStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpFillingStatus.getAmount()));

                        // Data of current refuelling - used for calculating progress-bars
                        item.setDispensedVolume(String.valueOf(pumpFillingStatus.getVolume()));
                        item.setDispensedAmount(String.valueOf(pumpFillingStatus.getAmount()));

                        StateData stateData = new StateData();
                        stateData.setText(String.valueOf(item.getNozzle()));
                        stateData.setViewModel(this);
                        stateData.setPumpItem(item);

                        mStateMachine.transition(new FuelingState(), stateData);

                        // Set pump status later than other data because previous status can be used in state transition
                        item.setStatus(pumpFillingStatus.getStatus());

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initPumpEndOfTransactionStatusListener() {
        DataHolder<PumpEndOfTransactionStatus> pumpEndOfTransactionStatusDataHolder = mDataStorage.getPumpEndOfTransactionStatusDataHolder();
        pumpEndOfTransactionStatusDataHolder.addOnDataChangeListener(pumpEndOfTransactionStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpEndOfTransactionStatus.getPump()) {
                        item.setStatus(pumpEndOfTransactionStatus.getStatus());
                        item.setStateDescription(pumpEndOfTransactionStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpEndOfTransactionStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpEndOfTransactionStatus.getStatus()));
                        item.setNozzle(pumpEndOfTransactionStatus.getNozzle());
                        item.setFuelName(pumpEndOfTransactionStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpEndOfTransactionStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpEndOfTransactionStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpEndOfTransactionStatus.getAmount()));

                        // close transaction here in case if “AutoCloseTransaction” set to false

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initPumpOfflineStatusListener() {
        DataHolder<PumpOfflineStatus> pumpOfflineStatusDataHolder = mDataStorage.getPumpOfflineStatusDataHolder();
        pumpOfflineStatusDataHolder.addOnDataChangeListener(pumpOfflineStatus -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpOfflineStatus.getPump()) {
                        item.setStatus(pumpOfflineStatus.getStatus());
                        item.setStateDescription(pumpOfflineStatus.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpOfflineStatus.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpOfflineStatus.getStatus()));
                        item.setNozzle(pumpOfflineStatus.getNozzle());
                        item.setFuelName(pumpOfflineStatus.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpOfflineStatus.getPrice()));
                        item.setVolume(String.valueOf(pumpOfflineStatus.getVolume()));
                        item.setAmount(String.valueOf(pumpOfflineStatus.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initPumpTotalsListener() {
        DataHolder<PumpTotals> pumpTotalsDataHolder = mDataStorage.getPumpTotalsDataHolder();
        pumpTotalsDataHolder.addOnDataChangeListener(pumpTotals -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == pumpTotals.getPump()) {
                        item.setStatus(pumpTotals.getStatus());
                        item.setStateDescription(pumpTotals.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(pumpTotals.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(pumpTotals.getStatus()));
                        item.setNozzle(pumpTotals.getNozzle());
                        item.setFuelName(pumpTotals.getFuelGradeName());
                        item.setPrice(String.valueOf(pumpTotals.getPrice()));
                        item.setVolume(String.valueOf(pumpTotals.getVolume()));
                        item.setAmount(String.valueOf(pumpTotals.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initPumpDisplayDataListener() {
        DataHolder<PumpDisplayData> pumpDisplayDataDataHolder = mDataStorage.getPumpDisplayDataDataHolder();
        pumpDisplayDataDataHolder.addOnDataChangeListener(displayData -> {
            List<PumpItem> currentList = mPumps.getValue();

            if (currentList != null) {
                for (PumpItem item : currentList) {
                    if (item.getNumber() == displayData.getPump()) {
                        item.setStatus(displayData.getStatus());
                        item.setStateDescription(displayData.getStatus().toString());
                        item.setStateColor(mPTSManager.getStatusColor(displayData.getStatus()));
                        item.setStateBackgroundColor(mPTSManager.getStatusBackgroundColor(displayData.getStatus()));
                        item.setNozzle(displayData.getLastNozzle());
                        item.setFuelName(displayData.getLastFuelGradeName());
                        item.setVolume(String.valueOf(displayData.getVolume()));
                        item.setAmount(String.valueOf(displayData.getAmount()));

                        break;
                    }
                }

                setPumps(currentList);
                mAnyPumpStatusInitialized = true;
                checkInitializationDataReady();
            }
        });
    }

    private void initMeasurementUnitsListener() {
        DataHolder<MeasurementUnits> measurementUnitsDataHolder = mDataStorage.getMeasurementUnitsDataHolder();
        measurementUnitsDataHolder.addOnDataChangeListener(measurementUnits -> {
            updateVolumeUnits(measurementUnits.getVolume());
        });
    }

    private void checkInitializationDataReady() {
        if (mPumpsInitialized && mNozzlesInitialized && mAnyPumpStatusInitialized) {
            mDataInitialized.postValue(true);
        }
    }

    private void resetInitializationDataReady() {
        mPumpsInitialized = false;
        mNozzlesInitialized = false;
        mAnyPumpStatusInitialized = false;
        mDataInitialized.postValue(false);
    }

    public void onNozzleClicked(View view) {
        boolean nozzleMustBeTaken = ApplicationFacade.getInstance().getSettings().getNozzleMustBeTaken();
        if(!nozzleMustBeTaken) {
            sendViewModelCommandEvent(chooseNozzleWithList.toString(), null);
        }
    }

    public void onRequestToNavigateToPump(PumpItem pumpItem) {
        CompletableFuture<Boolean> futureSwitchStateMachineToPumpSelectedState = CompletableFuture.supplyAsync(() -> switchStateMachineToPumpSelectedState(pumpItem));
        futureSwitchStateMachineToPumpSelectedState.thenAccept(result -> {
            if (result) {
                boolean nozzleMustBeTaken = ApplicationFacade.getInstance().getSettings().getNozzleMustBeTaken();
                if(nozzleMustBeTaken && pumpItem.getNozzle() == 0) {
                    sendViewModelCommandEvent(showNozzleIsNotPickedUpDialog.toString(), pumpItem);
                    return;
                }

                updateNozzlesForSelectedPumpItem(pumpItem);

                sendViewModelCommandEvent(navigateToPump.toString(), pumpItem);
            }
        });
    }

    public void updateNozzlesForSelectedPumpItem(PumpItem pumpItem) {
        if (pumpItem == null) {
            return;
        }

        List<PumpNozzles> pumpNozzlesConfiguration = mDataStorage.getPumpNozzlesConfiguration();
        if (pumpNozzlesConfiguration == null || pumpNozzlesConfiguration.isEmpty()) {
            return;
        }

        // Find PumpNozzles config for this pump
        PumpNozzles pumpNozzles = pumpNozzlesConfiguration.stream()
            .filter(obj -> obj != null && Objects.equals(obj.getPumpId(), pumpItem.getNumber()))
            .findFirst()
            .orElse(null);

        if (pumpNozzles == null) {
            return;
        }

        List<FuelGrade> fuelGradesConfiguration = mDataStorage.getFuelGradesConfiguration();
        if (fuelGradesConfiguration == null || fuelGradesConfiguration.isEmpty()) {
            return;
        }

        List<Integer> fuelGradeIds = pumpNozzles.getFuelGradeIds();
        if (fuelGradeIds == null || fuelGradeIds.isEmpty()) {
            return;
        }

        List<NozzleItem> nozzleItems = new ArrayList<>();

        for (int nozzleIndex = 0; nozzleIndex < fuelGradeIds.size(); ++nozzleIndex) {
            Integer fuelGradeId = fuelGradeIds.get(nozzleIndex);
            if (fuelGradeId == null) continue;

            FuelGrade fuelGrade = fuelGradesConfiguration.stream()
                .filter(obj -> obj != null && Objects.equals(obj.getId(), fuelGradeId))
                .findFirst()
                .orElse(null);

            if (fuelGrade != null) {
                NozzleItem nozzleItem = new NozzleItem();
                nozzleItem.setNozzleNumber(nozzleIndex + 1);
                nozzleItem.setFuelName(fuelGrade.getName());
                nozzleItem.setPrice(fuelGrade.getPrice());

                nozzleItems.add(nozzleItem);
            }
        }

        setNozzles(nozzleItems);
        chooseSelectedNozzleAutomatically(nozzleItems);
    }

    private void chooseSelectedNozzleAutomatically(List<NozzleItem> nozzles) {
        if (nozzles == null || nozzles.isEmpty()) {
            return;
        }

        PumpItem selectedPumpItem = getSelectedPump().getValue();
        if (selectedPumpItem == null) {
            return;
        }

        int upNozzleNumber = selectedPumpItem.getNozzle();

        // Try to find matching nozzle
        NozzleItem selectedNozzle = nozzles.stream()
            .filter(obj -> obj.getNozzleNumber() == upNozzleNumber)
            .findFirst()
            .orElse(nozzles.get(0)); // fallback to first

        StateData stateData = new StateData();
        stateData.setViewModel(this);
        stateData.setPumpItem(selectedPumpItem);
        stateData.setText(String.valueOf(selectedNozzle.getNozzleNumber()));

        boolean bRes = mStateMachine.transition(new NozzleSelectedState(), stateData);
        if (bRes) {
            setSelectedNozzle(selectedNozzle);
        }
    }

    public LiveData<List<PumpItem>> getPumps() {
        return mPumps;
    }

    public void setPumps(List<PumpItem> pumps) {
        mPumps.postValue(pumps);
        mDataStorage.setPumpItems(pumps);
    }

    public int getIndexOfPumpItem(PumpItem pumpItem) {
        List<PumpItem> pumps = mPumps.getValue();

        if (pumps == null) {
            return -1;
        }

        return pumps.indexOf(pumpItem);
    }

    public LiveData<PumpItem> getSelectedPump() {
        return mTwoWayFields.getSelectedPump();
    }

    public void setSelectedPump(PumpItem selectedPump) {
        mTwoWayFields.setSelectedPump(selectedPump);
    }

    public LiveData<List<NozzleItem>> getNozzles() {
        return mNozzles;
    }

    public void setNozzles(List<NozzleItem> nozzles) {
        mNozzles.postValue(nozzles);
        mDataStorage.setNozzleItems(nozzles);
    }

    public LiveData<NozzleItem> getSelectedNozzle() {
        return mTwoWayFields.getSelectedNozzle();
    }

    public void setSelectedNozzle(NozzleItem selectedNozzle) {
        mTwoWayFields.setSelectedNozzle(selectedNozzle);
    }

    public PumpsRecyclerViewAdapter getPumpsRecyclerViewAdapter() {
        return mPumpsRecyclerViewAdapter;
    }

    public NozzlesRecyclerViewAdapter getNozzlesRecyclerViewAdapter() {
        return mNozzlesRecyclerViewAdapter;
    }

    public boolean switchStateMachineToPumpSelectedState(PumpItem pumpItem) {
        if(pumpItem == null) {
            return false;
        }

        StateData stateData = new StateData();
        stateData.setText(String.valueOf(pumpItem.getNumber()));
        stateData.setViewModel(this);
        stateData.setPumpItem(pumpItem);

        return mStateMachine.transition(new PumpSelectedState(), stateData);
    }

    public void onApplyNozzleClicked(View view) {
        StateData stateData = new StateData();
        NozzleItem selectedNozzle = getSelectedNozzle().getValue();

        if(selectedNozzle != null) {
            stateData.setText(String.valueOf(Objects.requireNonNull(selectedNozzle).getNozzleNumber()));
        }

        stateData.setViewModel(this);
        stateData.setPumpItem(Objects.requireNonNull(getSelectedPump().getValue()));

        boolean bRes = mStateMachine.transition(new NozzleSelectedState(), stateData);

        if(bRes) {
            setSelectedNozzle(selectedNozzle);
        }

        sendViewModelCommandEvent(navigateUp.toString(), null);
    }

    public void onChangeVolumeClicked(View view) {
        sendViewModelCommandEvent(chooseVolumeWithKeyboard.toString(), null);
    }

    public void onPredefinedVolumeClicked(View view) {
        StateData stateData = prepareStateData(predefinedVolume.get());

        boolean bRes = mStateMachine.transition(new QuantitySelectedState(), stateData);

        if (bRes) {
            sendViewModelCommandEvent(showOrderConfirmation.toString(), null);
        }
    }

    public void updateVolumeUnits(String newVolumeUnits) {
        volumeUnits.set(newVolumeUnits);
    }

    public void onChangeAmountClicked(View view) {
        sendViewModelCommandEvent(chooseAmountWithKeyboard.toString(), null);
    }

    public void onPredefinedAmountClicked(View view) {
        StateData stateData = prepareStateData(predefinedAmount.get());

        boolean bRes = mStateMachine.transition(new CurrencySelectedState(), stateData);

        if (bRes) {
            sendViewModelCommandEvent(showOrderConfirmation.toString(), null);
        }
    }

    private StateData prepareStateData() {
        return prepareStateData("");
    }

    private StateData prepareStateData(String text) {
        StateData stateData = new StateData();
        stateData.setText(text);
        stateData.setViewModel(this);
        stateData.setPumpItem(mPTSManager.getDataStorage().getSelectedPump());

        return stateData;
    }

    public void onCancelClicked() {
        StateData stateData = prepareStateData();
        boolean bRes = mStateMachine.transition(new PumpNotSelectedState(), stateData);

        if (bRes) {
            sendViewModelCommandEvents(Arrays.asList(
                new EventCommand<>(ViewModelCommand.clearKeyboardStack.name(), null),
                new EventCommand<>(ViewModelCommand.navigateUp.name(), null)
            ));
        }
    }

    public void onConfirmClicked() {
        StateData stateData = prepareStateData();
        boolean bRes = mStateMachine.transition(new AuthorizingState(), stateData);

        if (bRes) {
            sendViewModelCommandEvents(Arrays.asList(
                new EventCommand<>(ViewModelCommand.clearKeyboardStack.name(), null),
                new EventCommand<>(ViewModelCommand.showOrdered.name(), null)
            ));
        }
    }

    public void onPumpStop() {
        StateData stateData = prepareStateData();
        mStateMachine.transition(new StoppingState(), stateData);

        sendViewModelCommandEvents(Arrays.asList(
            new EventCommand<>(ViewModelCommand.clearKeyboardStack.name(), null),
            new EventCommand<>(ViewModelCommand.navigateUp.name(), null)
        ));
    }
}